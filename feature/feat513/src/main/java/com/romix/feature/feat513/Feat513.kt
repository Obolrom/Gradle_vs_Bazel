package com.romix.feature.feat513

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat513Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat513UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat513FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat513UserSummary
)

data class Feat513UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat513NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat513Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat513Config = Feat513Config()
) {

    fun loadSnapshot(userId: Long): Feat513NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat513NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat513UserSummary {
        return Feat513UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat513FeedItem> {
        val result = java.util.ArrayList<Feat513FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat513FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat513UiMapper {

    fun mapToUi(model: List<Feat513FeedItem>): Feat513UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat513UiModel(
            header = UiText("Feat513 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat513UiModel =
        Feat513UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat513UiModel =
        Feat513UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat513UiModel =
        Feat513UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat513Service(
    private val repository: Feat513Repository,
    private val uiMapper: Feat513UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat513UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat513UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat513UserItem1(val user: CoreUser, val label: String)
data class Feat513UserItem2(val user: CoreUser, val label: String)
data class Feat513UserItem3(val user: CoreUser, val label: String)
data class Feat513UserItem4(val user: CoreUser, val label: String)
data class Feat513UserItem5(val user: CoreUser, val label: String)
data class Feat513UserItem6(val user: CoreUser, val label: String)
data class Feat513UserItem7(val user: CoreUser, val label: String)
data class Feat513UserItem8(val user: CoreUser, val label: String)
data class Feat513UserItem9(val user: CoreUser, val label: String)
data class Feat513UserItem10(val user: CoreUser, val label: String)

data class Feat513StateBlock1(val state: Feat513UiModel, val checksum: Int)
data class Feat513StateBlock2(val state: Feat513UiModel, val checksum: Int)
data class Feat513StateBlock3(val state: Feat513UiModel, val checksum: Int)
data class Feat513StateBlock4(val state: Feat513UiModel, val checksum: Int)
data class Feat513StateBlock5(val state: Feat513UiModel, val checksum: Int)
data class Feat513StateBlock6(val state: Feat513UiModel, val checksum: Int)
data class Feat513StateBlock7(val state: Feat513UiModel, val checksum: Int)
data class Feat513StateBlock8(val state: Feat513UiModel, val checksum: Int)
data class Feat513StateBlock9(val state: Feat513UiModel, val checksum: Int)
data class Feat513StateBlock10(val state: Feat513UiModel, val checksum: Int)

fun buildFeat513UserItem(user: CoreUser, index: Int): Feat513UserItem1 {
    return Feat513UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat513StateBlock(model: Feat513UiModel): Feat513StateBlock1 {
    return Feat513StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat513UserSummary> {
    val list = java.util.ArrayList<Feat513UserSummary>(users.size)
    for (user in users) {
        list += Feat513UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat513UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat513UiModel {
    val summaries = (0 until count).map {
        Feat513UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat513UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat513UiModel> {
    val models = java.util.ArrayList<Feat513UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat513AnalyticsEvent1(val name: String, val value: String)
data class Feat513AnalyticsEvent2(val name: String, val value: String)
data class Feat513AnalyticsEvent3(val name: String, val value: String)
data class Feat513AnalyticsEvent4(val name: String, val value: String)
data class Feat513AnalyticsEvent5(val name: String, val value: String)
data class Feat513AnalyticsEvent6(val name: String, val value: String)
data class Feat513AnalyticsEvent7(val name: String, val value: String)
data class Feat513AnalyticsEvent8(val name: String, val value: String)
data class Feat513AnalyticsEvent9(val name: String, val value: String)
data class Feat513AnalyticsEvent10(val name: String, val value: String)

fun logFeat513Event1(event: Feat513AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat513Event2(event: Feat513AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat513Event3(event: Feat513AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat513Event4(event: Feat513AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat513Event5(event: Feat513AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat513Event6(event: Feat513AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat513Event7(event: Feat513AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat513Event8(event: Feat513AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat513Event9(event: Feat513AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat513Event10(event: Feat513AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat513Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat513Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat513Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat513Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat513Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat513Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat513Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat513Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat513Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat513Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat513(u: CoreUser): Feat513Projection1 =
    Feat513Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat513Projection1> {
    val list = java.util.ArrayList<Feat513Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat513(u)
    }
    return list
}
