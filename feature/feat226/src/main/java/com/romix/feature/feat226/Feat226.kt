package com.romix.feature.feat226

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat226Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat226UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat226FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat226UserSummary
)

data class Feat226UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat226NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat226Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat226Config = Feat226Config()
) {

    fun loadSnapshot(userId: Long): Feat226NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat226NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat226UserSummary {
        return Feat226UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat226FeedItem> {
        val result = java.util.ArrayList<Feat226FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat226FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat226UiMapper {

    fun mapToUi(model: List<Feat226FeedItem>): Feat226UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat226UiModel(
            header = UiText("Feat226 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat226UiModel =
        Feat226UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat226UiModel =
        Feat226UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat226UiModel =
        Feat226UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat226Service(
    private val repository: Feat226Repository,
    private val uiMapper: Feat226UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat226UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat226UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat226UserItem1(val user: CoreUser, val label: String)
data class Feat226UserItem2(val user: CoreUser, val label: String)
data class Feat226UserItem3(val user: CoreUser, val label: String)
data class Feat226UserItem4(val user: CoreUser, val label: String)
data class Feat226UserItem5(val user: CoreUser, val label: String)
data class Feat226UserItem6(val user: CoreUser, val label: String)
data class Feat226UserItem7(val user: CoreUser, val label: String)
data class Feat226UserItem8(val user: CoreUser, val label: String)
data class Feat226UserItem9(val user: CoreUser, val label: String)
data class Feat226UserItem10(val user: CoreUser, val label: String)

data class Feat226StateBlock1(val state: Feat226UiModel, val checksum: Int)
data class Feat226StateBlock2(val state: Feat226UiModel, val checksum: Int)
data class Feat226StateBlock3(val state: Feat226UiModel, val checksum: Int)
data class Feat226StateBlock4(val state: Feat226UiModel, val checksum: Int)
data class Feat226StateBlock5(val state: Feat226UiModel, val checksum: Int)
data class Feat226StateBlock6(val state: Feat226UiModel, val checksum: Int)
data class Feat226StateBlock7(val state: Feat226UiModel, val checksum: Int)
data class Feat226StateBlock8(val state: Feat226UiModel, val checksum: Int)
data class Feat226StateBlock9(val state: Feat226UiModel, val checksum: Int)
data class Feat226StateBlock10(val state: Feat226UiModel, val checksum: Int)

fun buildFeat226UserItem(user: CoreUser, index: Int): Feat226UserItem1 {
    return Feat226UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat226StateBlock(model: Feat226UiModel): Feat226StateBlock1 {
    return Feat226StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat226UserSummary> {
    val list = java.util.ArrayList<Feat226UserSummary>(users.size)
    for (user in users) {
        list += Feat226UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat226UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat226UiModel {
    val summaries = (0 until count).map {
        Feat226UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat226UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat226UiModel> {
    val models = java.util.ArrayList<Feat226UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat226AnalyticsEvent1(val name: String, val value: String)
data class Feat226AnalyticsEvent2(val name: String, val value: String)
data class Feat226AnalyticsEvent3(val name: String, val value: String)
data class Feat226AnalyticsEvent4(val name: String, val value: String)
data class Feat226AnalyticsEvent5(val name: String, val value: String)
data class Feat226AnalyticsEvent6(val name: String, val value: String)
data class Feat226AnalyticsEvent7(val name: String, val value: String)
data class Feat226AnalyticsEvent8(val name: String, val value: String)
data class Feat226AnalyticsEvent9(val name: String, val value: String)
data class Feat226AnalyticsEvent10(val name: String, val value: String)

fun logFeat226Event1(event: Feat226AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat226Event2(event: Feat226AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat226Event3(event: Feat226AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat226Event4(event: Feat226AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat226Event5(event: Feat226AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat226Event6(event: Feat226AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat226Event7(event: Feat226AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat226Event8(event: Feat226AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat226Event9(event: Feat226AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat226Event10(event: Feat226AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat226Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat226Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat226Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat226Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat226Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat226Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat226Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat226Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat226Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat226Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat226(u: CoreUser): Feat226Projection1 =
    Feat226Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat226Projection1> {
    val list = java.util.ArrayList<Feat226Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat226(u)
    }
    return list
}
