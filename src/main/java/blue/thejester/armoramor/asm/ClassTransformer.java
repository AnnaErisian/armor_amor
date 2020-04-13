package blue.thejester.armoramor.asm;

import blue.thejester.armoramor.asm.names.ObfuscatedName;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class ClassTransformer implements IClassTransformer
{
	public static Logger logger = LogManager.getLogger("ArmorAmorCore");

	final String asmHandler = "blue/thejester/armoramor/asm/handler/AsmHandler";

	public static int transformations = 0;

	public ClassTransformer()
	{
		logger.log(Level.DEBUG, "Starting Class Transformation");
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
//		if (transformedName.equals("net.minecraft.util.CombatRules"))
//		{
//			transformations++;
//			return patchCombatRules(basicClass);
//		}
		if(transformedName.equals("c4.conarm.common.armor.utils.ArmorHelper")) {
			transformations++;
			return patchConarmArmorHelper(basicClass);
		}


		return basicClass;
	}

	private byte[] patchConarmArmorHelper(byte[] basicClass) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.DEBUG, "Found ArmorHelper Class: " + classNode.name);

		MethodNode getDefense = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals(new ObfuscatedName("getDefense").get()))
			{
				getDefense = mn;
			}
		}

		if (getDefense != null) {
			logger.log(Level.DEBUG, "- Found getDefense");

			for (int i = 0; i < getDefense.instructions.size(); i++)
			{
				AbstractInsnNode ain = getDefense.instructions.get(i);
				if (ain.getOpcode() == Opcodes.FRETURN)
				{
					InsnList toInsert = new InsnList();

					toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, asmHandler, "limitDefense", "(F)F", false));

					getDefense.instructions.insertBefore(ain, toInsert);

					i += 2;
				}
			}
		}

		CustomClassWriter writer = new CustomClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);

		return writer.toByteArray();
	}

	private byte[] patchCombatRules(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.DEBUG, "Found CombatRules Class: " + classNode.name);

		MethodNode getDamageAfterAbsorb = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals(new ObfuscatedName("func_189427_a").get()))
			{
				getDamageAfterAbsorb = mn;
			}
		}

		if (getDamageAfterAbsorb != null)
		{
			logger.log(Level.DEBUG, "- Found getDamageAfterAbsorb");

			for (int i = 0; i < getDamageAfterAbsorb.instructions.size(); i++)
			{
				AbstractInsnNode ain = getDamageAfterAbsorb.instructions.get(i);
				if (ain instanceof InsnNode) //before we start calculating toughness's effect
				{
					InsnNode insN = (InsnNode) ain;
					if (insN.getOpcode() == Opcodes.FCONST_2)
					{
						InsnList toInsert = new InsnList();
						toInsert.add(new VarInsnNode(Opcodes.FLOAD, 1));
						toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, asmHandler, "softCapArmor", "(F)F", false));
						toInsert.add(new VarInsnNode(Opcodes.FSTORE, 1));

						getDamageAfterAbsorb.instructions.insertBefore(insN, toInsert);

						break;
					}
				}
			}
		}

		CustomClassWriter writer = new CustomClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);

		return writer.toByteArray();
	}

	private byte[] patchDummyClass(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.DEBUG, "Found Dummy Class: " + classNode.name);

		CustomClassWriter writer = new CustomClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);

		return writer.toByteArray();
	}

	public int getNextIndex(MethodNode mn)
	{
		Iterator it = mn.localVariables.iterator();
		int max = 0;
		int next = 0;
		while (it.hasNext())
		{
			LocalVariableNode var = (LocalVariableNode) it.next();
			int index = var.index;
			if (index >= max)
			{
				max = index;
				next = max + Type.getType(var.desc).getSize();
			}
		}
		return next;
	}
}
