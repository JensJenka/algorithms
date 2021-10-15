package org.pg4200.ex05;

import org.pg4200.les05.MyMapBinarySearchTree;
import org.pg4200.les05.MyMapTreeBased;

import java.util.Objects;

public class TernaryTreeMap<K extends Comparable<K> ,V> implements MyMapTreeBased<K, V> {
    protected class TreeNode {

        public K rightKey;
        public V leftValue;

        public K leftKey;
        public V rightValue;

        public TreeNode left; //Pointer to the root of the subtree containing the values lower than this.value
        public TreeNode middle; //Pointer to the root of the subtree containing the values between left & right
        public TreeNode right; //Pointer to the root of the subtree containing the values larger than this.value
    }

    protected TreeNode root; //From the root of the tree, we can access all the nodes

    protected int size;

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key);
        root = put(key, value, root);
    }

    /**
     *  Add the V into the subtree, based on its K.
     *  Returns the (possibly new) root of this subtree
     */
    private TreeNode put(K key, V value, TreeNode subtree) {

        if (subtree == null) {
            TreeNode node = new TreeNode();
            node.leftKey = key;
            node.leftValue = value;
            size++;
            return node;
        }

        int cmp = key.compareTo(subtree.leftKey);

        if (cmp < 0) {
            subtree.left = put(key, value, subtree.left);
            return subtree;
        }

        if (cmp > 0) {

            if(subtree.rightKey == null) {
                subtree.rightKey = key;
                subtree.rightValue = value;
                size++;
            } else {
                int rightCmp = key.compareTo(subtree.rightKey);

                    if(rightCmp > 0) {
                        subtree.right = put(key, value, subtree.right);
                        return subtree;
                    }

                    if(rightCmp < 0) {
                        subtree.middle = put(key, value, subtree.middle);
                        return subtree;
                    }

                    assert rightCmp == 0;
                    subtree.rightValue = value;

                    return subtree;

            }
        }

        assert cmp == 0;        //Insert value is basically being overwritten since the K exists
        subtree.leftValue = value;

        return subtree;
    }


    @Override
    public void delete(K key) {
        Objects.requireNonNull(key);
        root = delete(key, root);
    }

    /**
     *  Delete the V from the subtree, based on its K.
     *  Returns the (possibly new) root of this subtree
     */
    protected TreeNode delete(K key, TreeNode subtreeRoot) {

        if (subtreeRoot == null) {
            /*
                This will happen when the key is not found, and we try a
                recursion on a null node.
                In this case, the new root of a null substree is still a null
                subtree, and we can return itself directly (ie null)
             */
            return null;
        }

        int cmp = key.compareTo(subtreeRoot.leftKey);

        if (cmp < 0) {
            subtreeRoot.left = delete(key, subtreeRoot.left);
            return subtreeRoot;
        }

        if (cmp > 0) {
            if (subtreeRoot.rightKey == null){
                return null;
            } else {
                int rightCmp = key.compareTo(subtreeRoot.rightKey);

                if(rightCmp > 0) {
                    subtreeRoot.right = delete(key, subtreeRoot.right);
                    return subtreeRoot;
                }
                if (rightCmp < 0) {
                    subtreeRoot.middle = delete(key, subtreeRoot.middle);
                    return subtreeRoot;
                }
                assert rightCmp == 0;
                size--;

                if (subtreeRoot.middle == null) {
                    return subtreeRoot.right;
                }
                if (subtreeRoot.right == null) {
                    return subtreeRoot.middle;
                }

            }
        }

        /*
            Here, we are done with the recursion.
            How to delete this node will depend on
            how many children it has
         */
        assert cmp == 0;

        size--;

        /*
            What we are going to do here depends on the number of children:
            0
            1 (left or right)
            2

            The (2) is the most complex case.
            For the (1), just need to check which child is not-null, and that will become
            the new subtree returned when "this" node is deleted.
            If both children are missing (ie case 0), then the subtree is just "this" node.
            Once deleted, what is left is just a null subtree.
            So we return null.
            However, we do not need to explicitely have a check like
            "if(subtreeRoot.left == null && subtreeRoot.right == null) return null"
            as the following check would give the same result (ie returning null because
            subtreeRoot.right is null) even in the (0) case.
         */

        if (subtreeRoot.left == null) {
            return subtreeRoot.right;
        }

        if (subtreeRoot.right == null) {
            return subtreeRoot.left;
        }

        /*
            Both children are present
         */
        assert subtreeRoot.left != null && subtreeRoot.right != null;

        TreeNode tmp = subtreeRoot;
        subtreeRoot = min(tmp.right);
        subtreeRoot.right = deleteMin(tmp.right);
        subtreeRoot.left = tmp.left;

        return subtreeRoot;
    }

    private TreeNode min(TreeNode subtreeRoot) {
        if (subtreeRoot.left == null) {
            return subtreeRoot;
        }
        return min(subtreeRoot.left);
    }

    private TreeNode deleteMin(TreeNode subtreeRoot) {

        if (subtreeRoot.left == null) {
            return subtreeRoot.right;
        }

        subtreeRoot.left = deleteMin(subtreeRoot.left);

        return subtreeRoot;
    }

    @Override
    public V get(K key) {
        Objects.requireNonNull(key);
        return get(key, root);
    }

    private V get(K key, TreeNode subtreeRoot) {

        if (subtreeRoot == null) {
            return null;
        }

        int cmp = key.compareTo(subtreeRoot.leftKey);

        if (cmp == 0) {
            return subtreeRoot.leftValue;
        } else if (cmp > 0) {
            //look at greater values in the right subtree
            return get(key, subtreeRoot.right);
        } else if (cmp < 0) {
            //look at smaller values in the left subtree
            return get(key, subtreeRoot.left);
        }

        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int getMaxTreeDepth() {

        if (root == null) {
            return 0;
        }

        return depth(root);
    }

    protected int depth(TreeNode node) {

        int leftDepth = 0;
        int rightDepth = 0;

        if (node.left != null) {
            leftDepth = depth(node.left);
        }
        if (node.right != null) {
            rightDepth = depth(node.right);
        }

        return 1 + Math.max(leftDepth, rightDepth);
    }


}
