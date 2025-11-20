package com.romix.feature.feat619

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat619Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat619UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat619FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat619UserSummary
)

data class Feat619UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat619NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat619Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat619Config = Feat619Config()
) {

    fun loadSnapshot(userId: Long): Feat619NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat619NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat619UserSummary {
        return Feat619UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat619FeedItem> {
        val result = java.util.ArrayList<Feat619FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat619FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat619UiMapper {

    fun mapToUi(model: List<Feat619FeedItem>): Feat619UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat619UiModel(
            header = UiText("Feat619 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat619UiModel =
        Feat619UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat619UiModel =
        Feat619UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat619UiModel =
        Feat619UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat619Service(
    private val repository: Feat619Repository,
    private val uiMapper: Feat619UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat619UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat619UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat619UserItem1(val user: CoreUser, val label: String)
data class Feat619UserItem2(val user: CoreUser, val label: String)
data class Feat619UserItem3(val user: CoreUser, val label: String)
data class Feat619UserItem4(val user: CoreUser, val label: String)
data class Feat619UserItem5(val user: CoreUser, val label: String)
data class Feat619UserItem6(val user: CoreUser, val label: String)
data class Feat619UserItem7(val user: CoreUser, val label: String)
data class Feat619UserItem8(val user: CoreUser, val label: String)
data class Feat619UserItem9(val user: CoreUser, val label: String)
data class Feat619UserItem10(val user: CoreUser, val label: String)

data class Feat619StateBlock1(val state: Feat619UiModel, val checksum: Int)
data class Feat619StateBlock2(val state: Feat619UiModel, val checksum: Int)
data class Feat619StateBlock3(val state: Feat619UiModel, val checksum: Int)
data class Feat619StateBlock4(val state: Feat619UiModel, val checksum: Int)
data class Feat619StateBlock5(val state: Feat619UiModel, val checksum: Int)
data class Feat619StateBlock6(val state: Feat619UiModel, val checksum: Int)
data class Feat619StateBlock7(val state: Feat619UiModel, val checksum: Int)
data class Feat619StateBlock8(val state: Feat619UiModel, val checksum: Int)
data class Feat619StateBlock9(val state: Feat619UiModel, val checksum: Int)
data class Feat619StateBlock10(val state: Feat619UiModel, val checksum: Int)

fun buildFeat619UserItem(user: CoreUser, index: Int): Feat619UserItem1 {
    return Feat619UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat619StateBlock(model: Feat619UiModel): Feat619StateBlock1 {
    return Feat619StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat619UserSummary> {
    val list = java.util.ArrayList<Feat619UserSummary>(users.size)
    for (user in users) {
        list += Feat619UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat619UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat619UiModel {
    val summaries = (0 until count).map {
        Feat619UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat619UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat619UiModel> {
    val models = java.util.ArrayList<Feat619UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat619AnalyticsEvent1(val name: String, val value: String)
data class Feat619AnalyticsEvent2(val name: String, val value: String)
data class Feat619AnalyticsEvent3(val name: String, val value: String)
data class Feat619AnalyticsEvent4(val name: String, val value: String)
data class Feat619AnalyticsEvent5(val name: String, val value: String)
data class Feat619AnalyticsEvent6(val name: String, val value: String)
data class Feat619AnalyticsEvent7(val name: String, val value: String)
data class Feat619AnalyticsEvent8(val name: String, val value: String)
data class Feat619AnalyticsEvent9(val name: String, val value: String)
data class Feat619AnalyticsEvent10(val name: String, val value: String)

fun logFeat619Event1(event: Feat619AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat619Event2(event: Feat619AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat619Event3(event: Feat619AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat619Event4(event: Feat619AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat619Event5(event: Feat619AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat619Event6(event: Feat619AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat619Event7(event: Feat619AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat619Event8(event: Feat619AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat619Event9(event: Feat619AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat619Event10(event: Feat619AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat619Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat619Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat619Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat619Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat619Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat619Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat619Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat619Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat619Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat619Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat619(u: CoreUser): Feat619Projection1 =
    Feat619Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat619Projection1> {
    val list = java.util.ArrayList<Feat619Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat619(u)
    }
    return list
}
