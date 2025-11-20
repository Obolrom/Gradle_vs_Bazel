package com.romix.feature.feat613

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat613Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat613UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat613FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat613UserSummary
)

data class Feat613UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat613NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat613Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat613Config = Feat613Config()
) {

    fun loadSnapshot(userId: Long): Feat613NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat613NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat613UserSummary {
        return Feat613UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat613FeedItem> {
        val result = java.util.ArrayList<Feat613FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat613FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat613UiMapper {

    fun mapToUi(model: List<Feat613FeedItem>): Feat613UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat613UiModel(
            header = UiText("Feat613 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat613UiModel =
        Feat613UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat613UiModel =
        Feat613UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat613UiModel =
        Feat613UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat613Service(
    private val repository: Feat613Repository,
    private val uiMapper: Feat613UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat613UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat613UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat613UserItem1(val user: CoreUser, val label: String)
data class Feat613UserItem2(val user: CoreUser, val label: String)
data class Feat613UserItem3(val user: CoreUser, val label: String)
data class Feat613UserItem4(val user: CoreUser, val label: String)
data class Feat613UserItem5(val user: CoreUser, val label: String)
data class Feat613UserItem6(val user: CoreUser, val label: String)
data class Feat613UserItem7(val user: CoreUser, val label: String)
data class Feat613UserItem8(val user: CoreUser, val label: String)
data class Feat613UserItem9(val user: CoreUser, val label: String)
data class Feat613UserItem10(val user: CoreUser, val label: String)

data class Feat613StateBlock1(val state: Feat613UiModel, val checksum: Int)
data class Feat613StateBlock2(val state: Feat613UiModel, val checksum: Int)
data class Feat613StateBlock3(val state: Feat613UiModel, val checksum: Int)
data class Feat613StateBlock4(val state: Feat613UiModel, val checksum: Int)
data class Feat613StateBlock5(val state: Feat613UiModel, val checksum: Int)
data class Feat613StateBlock6(val state: Feat613UiModel, val checksum: Int)
data class Feat613StateBlock7(val state: Feat613UiModel, val checksum: Int)
data class Feat613StateBlock8(val state: Feat613UiModel, val checksum: Int)
data class Feat613StateBlock9(val state: Feat613UiModel, val checksum: Int)
data class Feat613StateBlock10(val state: Feat613UiModel, val checksum: Int)

fun buildFeat613UserItem(user: CoreUser, index: Int): Feat613UserItem1 {
    return Feat613UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat613StateBlock(model: Feat613UiModel): Feat613StateBlock1 {
    return Feat613StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat613UserSummary> {
    val list = java.util.ArrayList<Feat613UserSummary>(users.size)
    for (user in users) {
        list += Feat613UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat613UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat613UiModel {
    val summaries = (0 until count).map {
        Feat613UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat613UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat613UiModel> {
    val models = java.util.ArrayList<Feat613UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat613AnalyticsEvent1(val name: String, val value: String)
data class Feat613AnalyticsEvent2(val name: String, val value: String)
data class Feat613AnalyticsEvent3(val name: String, val value: String)
data class Feat613AnalyticsEvent4(val name: String, val value: String)
data class Feat613AnalyticsEvent5(val name: String, val value: String)
data class Feat613AnalyticsEvent6(val name: String, val value: String)
data class Feat613AnalyticsEvent7(val name: String, val value: String)
data class Feat613AnalyticsEvent8(val name: String, val value: String)
data class Feat613AnalyticsEvent9(val name: String, val value: String)
data class Feat613AnalyticsEvent10(val name: String, val value: String)

fun logFeat613Event1(event: Feat613AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat613Event2(event: Feat613AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat613Event3(event: Feat613AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat613Event4(event: Feat613AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat613Event5(event: Feat613AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat613Event6(event: Feat613AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat613Event7(event: Feat613AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat613Event8(event: Feat613AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat613Event9(event: Feat613AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat613Event10(event: Feat613AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat613Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat613Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat613Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat613Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat613Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat613Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat613Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat613Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat613Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat613Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat613(u: CoreUser): Feat613Projection1 =
    Feat613Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat613Projection1> {
    val list = java.util.ArrayList<Feat613Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat613(u)
    }
    return list
}
